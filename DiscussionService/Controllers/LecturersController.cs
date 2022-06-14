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
                return Ok();
            }
            catch (DataException)
            {
                Console.WriteLine("Unable to save data");
                return StatusCode(500, "Internal server error");
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex);
                return StatusCode(500, "Internal server error");
            }

        }

        [HttpGet("{lecturerId}/questions")]
        [ServiceFilter(typeof(ValidationFilterAttribute))]
        public async Task<IActionResult> GetQuestionsRemovedByLecturer([FromRoute] Guid lecturerId)
        {
            try
            {
                var lecturerEmail = HttpContext.Items["userEmail"].ToString();
                var questions = await _repositoryWrapper.QuestionRepository.GetQuestionsRemovedByLecturer(lecturerEmail);
                // var lecturer = await _repositoryWrapper.LecturerRepository.GetLecturerByIdAsync(lecturerId);
                // foreach (var question in questions)
                // {
                //     question.RemovedBy = lecturer.Name;
                // }

                var result = _mapper.Map<List<GetQuestionDto>>(questions);
                return Ok(result);
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }
        }

        [HttpGet("{lecturerId}/answers")]
        [ServiceFilter(typeof(ValidationFilterAttribute))]
        public async Task<IActionResult> GetAnswersRemovedByLecturer([FromRoute] Guid lecturerId)
        {
            try
            {
                var lecturerEmail = HttpContext.Items["userEmail"].ToString();
                var answers = await _repositoryWrapper.AnswerRepository.GetAnswersRemovedByLecturer(lecturerEmail);
                // var lecturer = await _repositoryWrapper.LecturerRepository.GetLecturerByIdAsync(lecturerId);
                // foreach (var answer in answers)
                // {
                //     answer.RemovedBy = lecturer.Name;
                // }

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