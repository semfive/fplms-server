using AutoMapper;
using DiscussionService.Filters;
using DiscussionService.Contracts;
using DiscussionService.Dtos;
using DiscussionService.Models;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;

namespace DiscussionService.Controllers
{
    [ApiController]
    [Route("api/discussion/answers")]
    public class AnswersController : ControllerBase
    {
        private IRepositoryWrapper _repositoryWrapper;
        private IMapper _mapper;
        public AnswersController(IRepositoryWrapper repositoryWrapper, IMapper mapper)
        {
            _repositoryWrapper = repositoryWrapper;
            _mapper = mapper;
        }

        [HttpGet]
        [TypeFilter(typeof(AuthorizationFilterAttribute))]
        [ServiceFilter(typeof(ValidationFilterAttribute))]
        public async Task<IActionResult> GetAllAnswers([FromQuery] AnswersQueryStringParameters queryStringParameters)
        {
            try
            {
                var answers = await _repositoryWrapper.AnswerRepository.GetAllAnswersAsync(queryStringParameters);

                var metadata = new
                {
                    answers.TotalCount,
                    answers.PageSize,
                    answers.CurrentPage,
                    answers.HasPrevious,
                    answers.HasNext
                };

                Response.Headers.Add("X-Pagination", JsonConvert.SerializeObject(metadata));
                var result = _mapper.Map<List<GetAnswerDto>>(answers);

                return Ok(result);
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }
        }


        [HttpGet("{answerId}")]
        [TypeFilter(typeof(AuthorizationFilterAttribute))]
        public async Task<IActionResult> GetAnswerById([FromRoute] Guid answerId)
        {
            try
            {
                var answer = await _repositoryWrapper.AnswerRepository.GetAnswerByIdAsync(answerId);
                var result = _mapper.Map<GetAnswerDto>(answer);

                return Ok(result);
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }
        }


        [HttpPost]
        [TypeFilter(typeof(AuthorizationFilterAttribute))]
        [ServiceFilter(typeof(ValidationFilterAttribute))]
        public async Task<IActionResult> CreateAnswer([FromBody] CreateAnswerDto createAnswerDto)
        {
            try
            {
                var userEmail = HttpContext.Items["UserEmail"] as string;
                var userRole = HttpContext.Items["UserRole"] as string;

                if (!userRole.Equals("Student"))
                {
                    return Forbid("Only student can create answers.");
                }

                var answer = _mapper.Map<Answer>(createAnswerDto);
                var student = await _repositoryWrapper.StudentRepository.GetStudentByEmailAsync(userEmail);

                answer.Id = Guid.NewGuid();
                answer.StudentId = student.Id;

                _repositoryWrapper.AnswerRepository.CreateAnswer(answer);
                await _repositoryWrapper.SaveAsync();

                return Created("~api/discussion/answers/" + answer.Id, createAnswerDto);
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error:" + ex);
            }
        }

        [HttpPatch("{answerId}/upvote")]
        [TypeFilter(typeof(AuthorizationFilterAttribute))]
        public async Task<IActionResult> UpvoteAnswer([FromRoute] Guid answerId)
        {
            try
            {
                var userEmail = HttpContext.Items["UserEmail"] as string;
                var userRole = HttpContext.Items["UserRole"] as string;

                if (!userRole.Equals("Student"))
                {
                    return Forbid("Only student can upvote questions.");
                }

                var student = await _repositoryWrapper.StudentRepository.GetStudentByEmailAsync(userEmail);
                var answer = await _repositoryWrapper.AnswerRepository.GetAnswerByIdAsync(answerId);
                var author = await _repositoryWrapper.StudentRepository.GetStudentByIdAsync(answer.StudentId);

                var dto = new StudentAnswerUpvote()
                {
                    AnswerId = answer.Id,
                    StudentId = student.Id
                };
                var studentUpvote = await _repositoryWrapper.StudentAnswerUpvoteRepository.GetStudentAnswerUpvote(dto);

                if (studentUpvote != null)
                {
                    _repositoryWrapper.StudentAnswerUpvoteRepository.DeleteStudentAnswerUpvote(dto);
                    author.Point -= 1;
                    _repositoryWrapper.StudentRepository.Update(author);
                    await _repositoryWrapper.SaveAsync();
                    return NoContent();
                }

                _repositoryWrapper.StudentAnswerUpvoteRepository.CreateStudentAnswerUpvote(dto);
                author.Point += 1;
                _repositoryWrapper.StudentRepository.Update(author);
                await _repositoryWrapper.SaveAsync();
                return NoContent();
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }
        }

        [HttpPut("{answerId}/accept")]
        [TypeFilter(typeof(AuthorizationFilterAttribute))]
        [ServiceFilter(typeof(ValidationFilterAttribute))]
        public async Task<IActionResult> AcceptAnswer([FromRoute] Guid answerId)
        {
            try
            {
                var userEmail = HttpContext.Items["UserEmail"] as string;
                var userRole = HttpContext.Items["UserRole"] as string;

                if (!userRole.Equals("Student"))
                {
                    return Forbid("Only student can accept answers.");
                }

                var answer = await _repositoryWrapper.AnswerRepository.GetAnswerByIdAsync(answerId);
                var student = await _repositoryWrapper.StudentRepository.GetStudentByEmailAsync(userEmail);
                var question = await _repositoryWrapper.QuestionRepository.GetQuestionByIdAsync(answer.QuestionId, "eager");
                var answers = await _repositoryWrapper.AnswerRepository.GetAnswersByQuestionId(answer.QuestionId);

                if (answer == null)
                {
                    return NotFound();
                }

                if (student.Id != question.StudentId)
                {
                    return Forbid("Only the author of the question can accept the answer");
                }

                foreach (var a in answers.ToList())
                {
                    if (a.Id == answerId)
                    {
                        a.Accepted = !a.Accepted;
                        question.Solved = !a.Accepted;

                        if (a.Accepted == true)
                        {
                            var author = await _repositoryWrapper.StudentRepository.GetStudentByIdAsync(a.StudentId);
                            author.Point += 10;
                            _repositoryWrapper.StudentRepository.Update(author);
                        }
                        else
                        {
                            var author = await _repositoryWrapper.StudentRepository.GetStudentByIdAsync(a.StudentId);
                            author.Point -= 10;
                            _repositoryWrapper.StudentRepository.Update(author);
                        }
                    }
                    else
                    {
                        a.Accepted = false;
                    }
                    _repositoryWrapper.AnswerRepository.UpdateAnswer(_mapper.Map<Answer>(a));
                }
                // _repositoryWrapper.QuestionRepository.UpdateQuestion(_mapper.Map<Question>(_mapper.Map<UpdateQuestionSolveStatusDto>(question)));
                await _repositoryWrapper.SaveAsync();
                return NoContent();

            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }
        }

        [HttpPut("{answerId}")]
        [TypeFilter(typeof(AuthorizationFilterAttribute))]
        [ServiceFilter(typeof(ValidationFilterAttribute))]
        public async Task<IActionResult> UpdateAnswer([FromRoute] Guid answerId, [FromBody] UpdateAnswerDto updateAnswerDto)
        {
            try
            {
                var userEmail = HttpContext.Items["UserEmail"] as string;
                var userRole = HttpContext.Items["UserRole"] as string;

                if (!userRole.Equals("Student"))
                {
                    return Forbid("Only student can accept answers.");
                }

                var student = await _repositoryWrapper.StudentRepository.GetStudentByEmailAsync(userEmail);
                var newAnswer = _mapper.Map<Answer>(updateAnswerDto);
                var answer = await _repositoryWrapper.AnswerRepository.GetAnswerByIdAsync(answerId);

                if (answer == null)
                {
                    return NotFound();
                }

                if (!student.Id.Equals(answer.StudentId))
                {
                    return Forbid("Only the author of the question can update the question");
                }

                answer.Content = newAnswer.Content;
                answer.ModifiedDate = DateTime.Now;
                _repositoryWrapper.AnswerRepository.UpdateAnswer(answer);
                await _repositoryWrapper.SaveAsync();
                return NoContent();
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }
        }

        [HttpDelete("{answerId}")]
        [TypeFilter(typeof(AuthorizationFilterAttribute))]
        [ServiceFilter(typeof(ValidationFilterAttribute))]
        public async Task<IActionResult> DeleteAnswer([FromRoute] Guid answerId)
        {
            try
            {
                var userEmail = HttpContext.Items["UserEmail"] as string;
                var userRole = HttpContext.Items["UserRole"] as string;

                var answer = await _repositoryWrapper.AnswerRepository.GetAnswerByIdAsync(answerId);

                if (answer == null)
                {
                    return NotFound();
                }

                if (userRole == "Lecturer")
                {
                    answer.Removed = true;
                    answer.RemovedBy = userEmail;
                    _repositoryWrapper.AnswerRepository.UpdateAnswer(answer);
                    await _repositoryWrapper.SaveAsync();
                    return NoContent();
                }

                var student = await _repositoryWrapper.StudentRepository.GetStudentByEmailAsync(userEmail);
                if (!student.Id.Equals(answer.StudentId))
                {
                    return Forbid("Only the author of the answer or a lecturer can delete the answer");
                }

                answer.Removed = true;
                answer.RemovedBy = userEmail;
                _repositoryWrapper.AnswerRepository.UpdateAnswer(answer);
                await _repositoryWrapper.SaveAsync();
                return NoContent();
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }
        }
    }
}