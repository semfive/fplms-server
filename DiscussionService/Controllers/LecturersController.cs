using System.Data;
using AutoMapper;
using DiscussionService.Filters;
using DiscussionService.Contracts;
using DiscussionService.Dtos;
using DiscussionService.Models;
using Microsoft.AspNetCore.Mvc;

namespace DiscussionService.Controllers
{
    [ApiController]
    [Route("api/discussion/lecturers")]
    public class LecturersController : ControllerBase
    {

        private IMapper _mapper;
        private IRepositoryWrapper _repositoryWrapper;

        public LecturersController(IMapper mapper, IRepositoryWrapper repositoryWrapper)
        {
            _mapper = mapper;
            _repositoryWrapper = repositoryWrapper;
        }

        [HttpGet]
        [TypeFilter(typeof(AuthorizationFilterAttribute))]
        public async Task<IActionResult> GetAllLecturers()
        {
            try
            {
                var lecturers = await _repositoryWrapper.LecturerRepository.GetAllLecturersAsync();
                var result = _mapper.Map<List<GetLecturerDto>>(lecturers);

                return Ok(result);
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }
        }

        [HttpGet("{lecturerId}")]
        [TypeFilter(typeof(AuthorizationFilterAttribute))]
        public async Task<IActionResult> GetLecturerById([FromRoute] Guid lecturerId)
        {
            try
            {
                var lecturer = await _repositoryWrapper.LecturerRepository.GetLecturerByIdAsync(lecturerId);
                var result = _mapper.Map<GetLecturerDto>(lecturer);

                return Ok(result);
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }
        }

        [HttpPost]
        [ServiceFilter(typeof(ValidationFilterAttribute))]
        public async Task<IActionResult> CreateLecturer(CreateLecturerDto createLecturerDto)
        {
            try
            {
                Lecturer lecturer = _mapper.Map<Lecturer>(createLecturerDto);
                var lecturerExists = await _repositoryWrapper.LecturerRepository.GetLecturerByEmailAsync(lecturer.Email);
                if (lecturerExists != null)
                {
                    return Ok();
                }

                lecturer.Id = Guid.NewGuid();
                _repositoryWrapper.LecturerRepository.Create(lecturer);
                await _repositoryWrapper.SaveAsync();
                return Created("~api/discussion/lecturers/" + lecturer.Id, createLecturerDto);
            }
            catch (DataException ex)
            {
                return StatusCode(500, "Unable to save data");
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }

        }

        [HttpGet("questions")]
        [ServiceFilter(typeof(ValidationFilterAttribute))]
        public async Task<IActionResult> GetQuestionsRemovedByLecturer([FromRoute] Guid lecturerId)
        {
            try
            {
                var userEmail = HttpContext.Items["UserEmail"] as string;
                var userRole = HttpContext.Items["UserRole"] as string;

                if (!userRole.Equals("Lecturer"))
                {
                    return Forbid("Only lecturers can get questions.");
                }
                var lecturer = await _repositoryWrapper.LecturerRepository.GetLecturerByEmailAsync(userEmail);
                var questions = await _repositoryWrapper.QuestionRepository.GetQuestionsRemovedByLecturer(lecturer.Email);
                if (!lecturer.Email.Equals(questions.First().RemovedBy))
                {
                    return Forbid("Only the lecturer who removed the questions can get their questions");
                }

                var result = _mapper.Map<List<GetQuestionDto>>(questions);
                return Ok(result);
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }
        }

        [HttpGet("answers")]
        [ServiceFilter(typeof(ValidationFilterAttribute))]
        public async Task<IActionResult> GetAnswersRemovedByLecturer([FromRoute] Guid lecturerId)
        {
            try
            {
                var userEmail = HttpContext.Items["UserEmail"] as string;
                var userRole = HttpContext.Items["UserRole"] as string;

                if (!userRole.Equals("Lecturer"))
                {
                    return Forbid("Only lecturers can get questions.");
                }
                var lecturer = await _repositoryWrapper.LecturerRepository.GetLecturerByEmailAsync(userEmail);
                var answers = await _repositoryWrapper.AnswerRepository.GetAnswersRemovedByLecturer(lecturer.Email);
                if (!lecturer.Email.Equals(answers.First().RemovedBy))
                {
                    return Forbid("Only the lecturer who removed the answers can get their answers");
                }
                var result = _mapper.Map<List<GetAnswerDto>>(answers);
                return Ok(result);
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }
        }

    }
}